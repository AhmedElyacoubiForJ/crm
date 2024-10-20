package edu.yacoubi.crm.mapper;

public interface IMapper<A, B> {
    B mapTo(A source);
    A mapFrom(B source);
}
