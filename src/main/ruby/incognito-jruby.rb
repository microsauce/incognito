# TODO override array/hash methods

module Incognito

  class JRubyObjectProxy

    def initialize(target, this_runtime)
      @meta_object = target # meta_object
      @this_runtime = this_runtime
    end

    instance_methods.each { |m| undef_method m unless m =~ /(^__|^send$|^object_id$)/ }

    protected
      def method_missing(name, *args, &block)
        if origin_runtime.lang == RUBY
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

  def create_exec_proxy(target, this_runtime)
    return Proc.new { |*args|
      origin_runtime = target.origin_runtime
      this_runtime.proxy(origin_runtime.exec(target, args.collect { |it|
        origin_runtime.proxy(it)
      }))
    }
  end

end # module