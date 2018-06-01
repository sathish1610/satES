/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sat.ES;

import com.sat.email.EmailJobScheduler;

/**
 *
 * @author Sathishkumar T
 */
public class EmailScheduler {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        new EmailJobScheduler().process();
    }

}
