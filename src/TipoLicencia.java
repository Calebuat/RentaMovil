public enum TipoLicencia {
    A, B, C, M;

    public boolean habilita(TipoLicencia requerida) {
        if (requerida == null) {
            throw new IllegalArgumentException("La licencia requerida es obligatoria");
        }
        if (this == A) return requerida == A || requerida == B || requerida == C;
        if (this == B) return requerida == B || requerida == C;
        if (this == C) return requerida == C;
        return requerida == M;
    }
}
