package com.sist.aop;
import java.util.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sist.dao.*;
@Aspect
@Component
public class EmpAOP {
   @Autowired
   private EmpDAO dao;
   // 시작과 동시에 처리 ==> 공통적으로 사용 
   /*
    *     public void insert()
    *     {
    *         SqlSession session=null;
    *         try
    *         {
    *             session=ssf.openSession() => before
    *             session.insert("id")
    *         }catch(Exception ex)
    *         {
    *             ex.printStackTrace() => afterThrowing
    *         }
    *         finally
    *         {
    *             if(session!=null) => after
    *               session.close()
    *         }
    *     }
    *     
    *     public void aaa()
    *     {
    *        ==== @Before => getConnection()
    *        try
    *        {
    *            ========= @Around => setAutoCommit(false)
    *            소스 코딩 
    *            ========= commit()
    *        }catch(Exception ex)
    *        {
    *            @AfterThrowing
    *        }
    *        finally
    *        {
    *            @After
    *        }
    *        return  => @AfterReturning
    *     }
    */
   @Before("execution(* com.sist.dao.EmpDAO.emp*(..))")
   // try 첫번째줄
   public void getConnection()
   {
	   dao.getConnection();
   }
   // finally에 추가 
   @After("execution(* com.sist.dao.EmpDAO.emp*(..))")
   public void disConnection()
   {
	   dao.disConnection();
   }
   // log 파일 , 트랜잭션 , 보안 
   @Around(value = "execution(* com.sist.dao.EmpDAO.emp*(..))")
   public Object log(ProceedingJoinPoint jp)
   {
	   Object obj=null;
	   try
	   {
	      long start=System.currentTimeMillis();
	      obj=jp.proceed();
	      System.out.println("호출된 메소드:"+jp.getSignature().getName());
	      long end=System.currentTimeMillis();
	      System.out.println("소요시간:"+(end-start));
	   }catch(Throwable ex) {}
	   return obj;
   }
   // 정상수행 => return값을 받아서 처리 
   @AfterReturning(value = "execution(* com.sist.dao.EmpDAO.emp*())", 
		   returning = "obj")
   public void afterReturning(Object obj)
   {
	   List<EmpVO> list=(List<EmpVO>)obj;
	   for(EmpVO vo:list)
	   {
		   System.out.println(vo.getEmpno()+" "
				   +vo.getEname()+" "
				   +vo.getJob()+" "
				   +vo.getSal());
	   }
   }
   // catch 영역 
   @AfterThrowing(value = "execution(* com.sist.dao.EmpDAO.emp*(..))",throwing = "ex")
   public void afterThrowing(Throwable ex) throws Throwable
   {
	   System.out.println("======== 오류 발생 =======");
	   ex.printStackTrace();
   }
   
}
