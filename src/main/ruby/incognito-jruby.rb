# TODO override array/hash methods

require 'java'
require 'date'

java_import org.microsauce.incognito.CommonDate
java_import org.microsauce.incognito.Runtime

module Incognito

  class JRubyObjectProxy

    def initialize(target, this_runtime)
      @meta_object = target # meta_object
      @this_runtime = this_runtime
    end

    # TODO review this.  Might want to be more selective on what we un-define
    instance_methods.each { |m| undef_method m unless m =~ /(^__|^send$|^object_id$)/ }

    protected
      def method_missing(name, *args, &block)
        if origin_runtime.id == Runtime::RT::RUBY
          @meta_object.target.send name, *args
        else
          @meta_object.origin_runtime.exec_method name, prepare_arguments(args)
        end
      end

      def prepare_arguments(args)
        args.collect { |it|
          @meta_object.origin_runtime.proxy(it)
        }
      end
  end

  def create_obj_proxy(target, this_runtime)
    return JRubyObjectProxy.new(target, this_runtime)
  end

  def create_exec_proxy(meta_object, this_runtime)
    if origin_runtime.lang == RUBY
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
     return CommonDate.new(date.year, date.month, date.mday, date.hour, date.second, 0)
  end

  def exec_proc(p, *args)
    return p.call(*args)
  end

end # module Incognito