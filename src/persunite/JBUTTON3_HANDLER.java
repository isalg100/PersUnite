    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        
        // =====================================================
        // GET AGENT TABLE MANAGER
        // =====================================================
        
        AgentTableManager agentManager = new AgentTableManager(AgentTable);
        
        // =====================================================
        // VALIDATE SELECTION
        // =====================================================
        
        if (!agentManager.hasSelection()) {
            JOptionPane.showMessageDialog(
                this,
                "⚠️ Veuillez sélectionner un agent",
                "Sélection requise",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // =====================================================
        // GET SELECTED AGENT INFO
        // =====================================================
        
        String selectedMatrim = agentManager.getSelectedMatrim();
        String agentName = agentManager.getSelectedAgentName();
        int uniteId = UserSession.uniteId;
        
        // =====================================================
        // VALIDATION
        // =====================================================
        
        if (selectedMatrim == null || selectedMatrim.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Erreur: MATRIM introuvable",
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        System.out.println("========================================");
        System.out.println("📋 OPENING EVENT MANAGEMENT");
        System.out.println("AGENT: " + agentName);
        System.out.println("MATRIM: " + selectedMatrim);
        System.out.println("UNIT: " + uniteId);
        System.out.println("========================================");
        
        // =====================================================
        // OPEN EVENT MANAGEMENT DIALOG
        // =====================================================
        
        EventManagementDialog dialog = new EventManagementDialog(
            this,
            selectedMatrim,
            agentName,
            uniteId
        );
        
        dialog.setVisible(true);
        
    }//GEN-LAST:event_jButton3ActionPerformed
