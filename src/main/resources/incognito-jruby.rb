# TODO override array/hash methods

class IncognitoProxy

  def initialize(obj_adaptor)
    @obj_adaptor = obj_adaptor
  end
  instance_methods.each { |m| undef_method m unless m =~ /(^__|^send$|^object_id$)/ }

  protected
    def method_missing(name, *args, &block)
      #@obj_adaptor.
    end
end