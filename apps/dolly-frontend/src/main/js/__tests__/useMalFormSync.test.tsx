import { describe, expect, it, vi } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { useForm } from 'react-hook-form'
import { useMalFormSync } from '@/components/bestillingsveileder/stegVelger/hooks/useMalFormSync'
import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'

type FormValues = Record<string, any>

const createMockContext = (
	overrides: Partial<BestillingsveilederContextType> = {},
): BestillingsveilederContextType =>
	({
		mal: undefined,
		setIdenttype: vi.fn(),
		updateContext: vi.fn(),
		...overrides,
	}) as any

const createMockMal = (bestillingOverrides = {}) => ({
	id: 'test-mal-123',
	malNavn: 'Test Mal',
	bestilling: {
		antall: 5,
		environments: ['q1', 'q2'],
		pdldata: {
			opprettNyPerson: {
				identtype: 'DNR',
				syntetisk: true,
				id2032: true,
			},
			person: {
				sivilstand: [{ type: 'GIFT' }],
			},
		},
		...bestillingOverrides,
	},
})

describe('useMalFormSync', () => {
	describe('nyBestilling scenarios', () => {
		it('should sync mal values into form when mal is set', async () => {
			const mal = createMockMal()
			const context = createMockContext({ mal })
			const is = { nyBestilling: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({
					defaultValues: {
						antall: 1,
						pdldata: { opprettNyPerson: { identtype: 'FNR' } },
						gruppevalg: 'test-gruppe',
						bruker: 'test-bruker',
						malBruker: 'test-mal-bruker',
					},
				})
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.mal).toBe('test-mal-123')
				expect(values.pdldata.opprettNyPerson.identtype).toBe('DNR')
				expect(values.pdldata.opprettNyPerson.id2032).toBe(true)
				expect(values.antall).toBe(5)
			})
		})

		it('should update context identtype and id2032 for nyBestilling', async () => {
			const setIdenttype = vi.fn()
			const updateContext = vi.fn()
			const mal = createMockMal()
			const context = createMockContext({ mal, setIdenttype, updateContext })
			const is = { nyBestilling: true }

			renderHook(() => {
				const formMethods = useForm<FormValues>({ defaultValues: {} })
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				expect(setIdenttype).toHaveBeenCalledWith('DNR')
				expect(updateContext).toHaveBeenCalledWith({ identtype: 'DNR' })
				expect(updateContext).toHaveBeenCalledWith({ id2032: true })
			})
		})

		it('should reset form to clean state when mal is cleared', async () => {
			const is = { nyBestilling: true }

			const { result, rerender } = renderHook(
				({ ctx }) => {
					const formMethods = useForm<FormValues>({
						defaultValues: {
							antall: 10,
							pdldata: { opprettNyPerson: { identtype: 'DNR' } },
							gruppeId: 5,
						},
					})
					useMalFormSync({ context: ctx, formMethods, is })
					return formMethods
				},
				{ initialProps: { ctx: createMockContext({ mal: createMockMal() }) } },
			)

			await waitFor(() => {
				expect(result.current.getValues().mal).toBe('test-mal-123')
			})

			rerender({ ctx: createMockContext({ mal: undefined }) })

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.mal).toBeUndefined()
				expect(values.antall).toBe(1)
				expect(values.pdldata.opprettNyPerson.identtype).toBe('FNR')
			})
		})

		it('should preserve gruppevalg, bruker, and malBruker when switching mal', async () => {
			const mal = createMockMal()
			const context = createMockContext({ mal })
			const is = { nyBestilling: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({
					defaultValues: {
						gruppevalg: 'original-gruppe',
						bruker: 'original-bruker',
						malBruker: 'original-mal-bruker',
					},
				})
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.gruppevalg).toBe('original-gruppe')
				expect(values.bruker).toBe('original-bruker')
				expect(values.malBruker).toBe('original-mal-bruker')
			})
		})
	})

	describe('leggTil scenarios', () => {
		it('should sanitize pdldata.opprettNyPerson for leggTil', async () => {
			const mal = createMockMal()
			const context = createMockContext({ mal })
			const is = { leggTil: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({ defaultValues: { gruppeId: 10 } })
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.pdldata?.opprettNyPerson).toBeUndefined()
				expect(values.pdldata?.person).toBeDefined()
				expect(values.gruppeId).toBe(10)
			})
		})

		it('should sanitize opprettNyPerson even when other pdldata fields dont exist', async () => {
			const mal = createMockMal({ pdldata: { opprettNyPerson: { identtype: 'FNR' } } })
			const context = createMockContext({ mal })
			const is = { leggTilPaaGruppe: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({ defaultValues: { gruppeId: 20 } })
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.pdldata?.opprettNyPerson).toBeUndefined()
			})
		})
	})

	describe('importTestnorge scenarios', () => {
		it('should preserve importPersoner array and sanitize pdldata', async () => {
			const mal = createMockMal()
			const context = createMockContext({ mal })
			const is = { importTestnorge: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({
					defaultValues: {
						importPersoner: [{ ident: '12345678901' }, { ident: '10987654321' }],
						gruppeId: 15,
					},
				})
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.importPersoner).toHaveLength(2)
				expect(values.pdldata?.opprettNyPerson).toBeUndefined()
			})
		})
	})

	describe('opprettFraIdenter scenarios', () => {
		it('should preserve opprettFraIdenter array and sanitize pdldata', async () => {
			const mal = createMockMal()
			const context = createMockContext({ mal })
			const is = { opprettFraIdenter: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({
					defaultValues: {
						opprettFraIdenter: ['123', '456', '789'],
						gruppeId: 25,
					},
				})
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.opprettFraIdenter).toHaveLength(3)
				expect(values.pdldata?.opprettNyPerson).toBeUndefined()
			})
		})
	})

	describe('edge cases', () => {
		it('should not update when mal id remains the same', async () => {
			const mal = createMockMal()
			const context = createMockContext({ mal })
			const is = { nyBestilling: true }

			const { result, rerender } = renderHook(() => {
				const formMethods = useForm<FormValues>({ defaultValues: { antall: 1 } })
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				expect(result.current.getValues().mal).toBe('test-mal-123')
			})

			result.current.setValue('antall', 99)

			rerender()

			expect(result.current.getValues().antall).toBe(99)
		})

		it('should handle mal with missing antall field', async () => {
			const mal = createMockMal({ antall: undefined })
			const context = createMockContext({ mal })
			const is = { nyBestilling: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({ defaultValues: { antall: 7 } })
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			await waitFor(() => {
				const values = result.current.getValues()
				expect(values.antall).toBe(7)
			})
		})

		it('should handle error gracefully when mal is malformed', async () => {
			const malformedMal = { id: 'bad-mal' } as any
			const context = createMockContext({ mal: malformedMal })
			const is = { nyBestilling: true }

			const { result } = renderHook(() => {
				const formMethods = useForm<FormValues>({ defaultValues: { antall: 1 } })
				useMalFormSync({ context, formMethods, is })
				return formMethods
			})

			expect(result.current.getValues().antall).toBe(1)
		})
	})
})
