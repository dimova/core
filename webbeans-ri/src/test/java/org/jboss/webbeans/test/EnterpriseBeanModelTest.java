package org.jboss.webbeans.test;

import static org.jboss.webbeans.test.util.Util.getEmptyAnnotatedType;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.webbeans.Current;
import javax.webbeans.Dependent;

import org.jboss.webbeans.introspector.AnnotatedType;
import org.jboss.webbeans.introspector.SimpleAnnotatedType;
import org.jboss.webbeans.model.bean.AbstractEnterpriseBeanModel;
import org.jboss.webbeans.model.bean.EnterpriseBeanModel;
import org.jboss.webbeans.test.annotations.Synchronous;
import org.jboss.webbeans.test.beans.Bear;
import org.jboss.webbeans.test.beans.Cheetah;
import org.jboss.webbeans.test.beans.Cougar;
import org.jboss.webbeans.test.beans.Elephant;
import org.jboss.webbeans.test.beans.Giraffe;
import org.jboss.webbeans.test.beans.Leopard;
import org.jboss.webbeans.test.beans.Lion;
import org.jboss.webbeans.test.beans.Panther;
import org.jboss.webbeans.test.beans.Puma;
import org.jboss.webbeans.test.beans.Tiger;
import org.jboss.webbeans.util.Reflections;
import org.testng.annotations.Test;

public class EnterpriseBeanModelTest extends AbstractTest
{  
   
   @Test @SpecAssertion(section="2.7.2")
   public void testSingleStereotype()
   {
	   assert false;
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testStateless()
   {
      EnterpriseBeanModel<Lion> lion = new EnterpriseBeanModel<Lion>(new SimpleAnnotatedType<Lion>(Lion.class), getEmptyAnnotatedType(Lion.class), manager);
      assert lion.getScopeType().equals(Dependent.class);
      Reflections.annotationSetMatches(lion.getBindingTypes(), Current.class);
      assert lion.getName().equals("lion");
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testStatelessDefinedInXml()
   {
      Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();
      AnnotatedType annotatedItem = new SimpleAnnotatedType(Giraffe.class, annotations);
      
      EnterpriseBeanModel<Giraffe> giraffe = new EnterpriseBeanModel<Giraffe>(new SimpleAnnotatedType(Giraffe.class), annotatedItem, manager);
      assert giraffe.getScopeType().equals(Dependent.class);
      Reflections.annotationSetMatches(giraffe.getBindingTypes(), Current.class);
   }
   
   @Test
   public void testStatelessWithRequestScope()
   {
      boolean exception = false;
      try
      {
         new EnterpriseBeanModel<Bear>(new SimpleAnnotatedType<Bear>(Bear.class), getEmptyAnnotatedType(Bear.class), manager);
      }
      catch (Exception e) 
      {
         exception = true;
      }
      assert exception;
   }
   
   @Test(groups="ejb3")
   public void testSingleton()
   {
      assert false;
   }
   
   @Test(groups="ejb3")
   public void testSingletonWithRequestScope()
   {
      assert false;
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testStateful()
   {

      AbstractEnterpriseBeanModel<Tiger> tiger = new EnterpriseBeanModel<Tiger>(new SimpleAnnotatedType(Tiger.class), getEmptyAnnotatedType(Tiger.class), manager);
      Reflections.annotationSetMatches(tiger.getBindingTypes(), Synchronous.class);
      assert tiger.getRemoveMethod().getAnnotatedItem().getDelegate().getName().equals("remove");
      assert tiger.getName() == null;
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testMultipleRemoveMethodsWithDestroys()
   {

      AbstractEnterpriseBeanModel<Elephant> elephant = new EnterpriseBeanModel<Elephant>(new SimpleAnnotatedType(Elephant.class), getEmptyAnnotatedType(Elephant.class), manager);
      assert elephant.getRemoveMethod().getAnnotatedItem().getDelegate().getName().equals("remove2");
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testMultipleRemoveMethodsWithoutDestroys()
   {
      boolean exception = false;
      try
      {
         new EnterpriseBeanModel<Puma>(new SimpleAnnotatedType(Puma.class), getEmptyAnnotatedType(Puma.class), manager);
      }
      catch (Exception e) 
      {
         exception = true;
      }
      assert exception;
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testMultipleRemoveMethodsWithMultipleDestroys()
   {
      boolean exception = false;
      try
      {
         new EnterpriseBeanModel<Cougar>(new SimpleAnnotatedType(Cougar.class), getEmptyAnnotatedType(Cougar.class), manager);
      }
      catch (Exception e) 
      {
         exception = true;
      }
      assert exception;
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testNonStatefulEnterpriseBeanWithDestroys()
   {
      boolean exception = false;
      try
      {
         new EnterpriseBeanModel<Cheetah>(new SimpleAnnotatedType(Cheetah.class), getEmptyAnnotatedType(Cheetah.class), manager);
      }
      catch (Exception e) 
      {
         exception = true;
      }
      assert exception;
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testRemoveMethodWithDefaultBinding()
   {

      AbstractEnterpriseBeanModel<Panther> panther = new EnterpriseBeanModel<Panther>(new SimpleAnnotatedType<Panther>(Panther.class), getEmptyAnnotatedType(Panther.class), manager);
      
      assert panther.getRemoveMethod().getAnnotatedItem().getDelegate().getName().equals("remove");
      assert panther.getRemoveMethod().getParameters().size() == 1;
      assert panther.getRemoveMethod().getParameters().get(0).getType().equals(String.class);
      assert panther.getRemoveMethod().getParameters().get(0).getBindingTypes().size() == 1;
      assert Reflections.annotationSetMatches(panther.getRemoveMethod().getParameters().get(0).getBindingTypes(), Current.class);
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testMessageDriven()
   {
      AbstractEnterpriseBeanModel<Leopard> leopard = new EnterpriseBeanModel<Leopard>(new SimpleAnnotatedType(Leopard.class), getEmptyAnnotatedType(Leopard.class), manager);
      Reflections.annotationSetMatches(leopard.getBindingTypes(), Current.class);
   }

}
