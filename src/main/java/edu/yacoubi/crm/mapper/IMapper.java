package edu.yacoubi.crm.mapper;
// wird nicht verwendet
public interface IMapper<A, B> {
    B mapTo(A source);
    A mapFrom(B source);
}
