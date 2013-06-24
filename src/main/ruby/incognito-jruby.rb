
require 'java'
require 'date'

java_import org.microsauce.incognito.CommonDate
java_import org.microsauce.incognito.Runtime
java_import org.microsauce.incognito.Type
java_import java.util.HashSet

class JRubyIncognito

  class JRubyObjectProxy

    def initialize(target, this_runtime)
      @meta_object = target # meta_object
      @this_runtime = this_runtime
    end

    # TODO review this.  Might want to be more selective on what we un-define
    instance_methods.each { |m| undef_method m unless m =~ /(^__|^send$|^to_s$|^object_id$|^kind_of\?$|^respond_to\?$)/ }

    def respond_to?(method, include_private = false)
      if @meta_object.origin_runtime.id == Runtime::ID::JRUBY
        return @meta_object.target_object.respond_to?(method, include_private)
      else
        return @meta_object.origin_runtime.respond_to(@meta_object, method)
      end
    end

    protected
      def method_missing(name, *args, &block) # TODO test block support

        if @meta_object.origin_runtime.id == Runtime::ID::JRUBY
          return @meta_object.target.send name, *args
        else
          member = @meta_object.origin_runtime.get_member(@meta_object, name)

          if not member.type.equals(Type::METHOD)
            name_str = name.to_s
            if name_str.end_with?('=')
              @meta_object.origin_runtime.setProp(@meta_object, name_str[0..name_str.length-2], @this_runtime.wrap(args[0]))
            else
              return @this_runtime.proxy(member)
            end
          else
            if not block.nil?
              args << block
            end
            return @this_runtime.proxy(member.origin_runtime.exec(member, nil, prepare_arguments(args)))
          end
        end
      end

      def prepare_arguments(args)
        args.collect { |it|
          @meta_object.origin_runtime.proxy(@this_runtime.wrap(it))
        }
      end

  end

  def create_obj_proxy(target, this_runtime)
    return JRubyObjectProxy.new(target, this_runtime)
  end

  def to_sym(str)
	return str.to_sym
  end

  def create_exec_proxy(meta_object, this_runtime)
    if @meta_object.origin_runtime.id == Runtime::ID::JRUBY
      return meta_object.target
    else
      return Proc.new { |*args|
        origin_runtime = meta_object.origin_runtime
        this_runtime.proxy(origin_runtime.exec(meta_object, args.collect { |it|
          origin_runtime.proxy(it)
        }))
      }
    end
  end

  def create_ruby_date(meta_object)
    if origin_runtime.lang == RUBY
      return meta_object.target_raw
    else
      cd = meta_object.target_object
      return DateTime.new(cd.year, cd.month, cd.day_of_month, cd.hour, cd.minute, cd.second, 0)
    end
  end

  def convert_date(date)
     return CommonDate.new(date.year, date.month, date.mday, date.hour, date.min, date.second, 0)
  end

  def exec_proc(p, *args)
    return p.call(*args)
  end

  def target_respond_to(target, identifier)
    return target.respond_to?(identifier)
  end

  def target_members(target) # TODO remove '' from members set ???
    set = HashSet.new
    user_methods  = (target.methods - Object.methods) #.collect { |it| return it }
    user_methods.each { |it|
      set.add(it.to_s.tr('^A-Za-z0-9_', ''))
    }
    set
  end

  def method_arity(target, method)
    target.class.instance_method(method.to_sym).arity
  end

  def target_to_s(target)
    target.to_s
  end

end # class Incognito

jruby_incognito = JRubyIncognito.new
