import { describe, expect, it } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { useForm } from 'react-hook-form'
import {
	useId2032Sync,
	useIdenttypeSync,
} from '@/components/bestillingsveileder/stegVelger/hooks/useFormFieldSync'
import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'

const createMockContext = (
	overrides: Partial<BestillingsveilederContextType> = {},
): BestillingsveilederContextType =>
	({
		identtype: 'FNR',
		id2032: false,
		...overrides,
	}) as any

describe('useIdenttypeSync', () => {
	it('should sync identtype from context to form', async () => {
		const context = createMockContext({ identtype: 'DNR' })
		const is = { nyBestilling: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { identtype: 'FNR' } },
				},
			})
			useIdenttypeSync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		await waitFor(() => {
			const identtype = result.current.getValues('pdldata.opprettNyPerson.identtype')
			expect(identtype).toBe('DNR')
		})
	})

	it('should update form when context identtype changes', async () => {
		const is = { nyBestilling: true }
		const erOrganisasjon = false

		const { result, rerender } = renderHook(
			({ ctx }) => {
				const formMethods = useForm({
					defaultValues: {
						pdldata: { opprettNyPerson: { identtype: 'FNR' } },
					},
				})
				useIdenttypeSync({ context: ctx, formMethods, erOrganisasjon, is })
				return formMethods
			},
			{ initialProps: { ctx: createMockContext({ identtype: 'FNR' }) } },
		)

		expect(result.current.getValues('pdldata.opprettNyPerson.identtype')).toBe('FNR')

		rerender({ ctx: createMockContext({ identtype: 'BOST' }) })

		await waitFor(() => {
			expect(result.current.getValues('pdldata.opprettNyPerson.identtype')).toBe('BOST')
		})
	})

	it('should not sync when erOrganisasjon is true', async () => {
		const context = createMockContext({ identtype: 'DNR' })
		const is = { nyBestilling: true }
		const erOrganisasjon = true

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { identtype: 'FNR' } },
				},
			})
			useIdenttypeSync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.identtype')).toBe('FNR')
	})

	it('should not sync when is.leggTil is true', async () => {
		const context = createMockContext({ identtype: 'DNR' })
		const is = { leggTil: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { identtype: 'FNR' } },
				},
			})
			useIdenttypeSync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.identtype')).toBe('FNR')
	})

	it('should not sync when is.leggTilPaaGruppe is true', async () => {
		const context = createMockContext({ identtype: 'DNR' })
		const is = { leggTilPaaGruppe: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { identtype: 'FNR' } },
				},
			})
			useIdenttypeSync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.identtype')).toBe('FNR')
	})

	it('should not sync when is.importTestnorge is true', async () => {
		const context = createMockContext({ identtype: 'DNR' })
		const is = { importTestnorge: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { identtype: 'FNR' } },
				},
			})
			useIdenttypeSync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.identtype')).toBe('FNR')
	})

	it('should not sync when is.opprettFraIdenter is true', async () => {
		const context = createMockContext({ identtype: 'DNR' })
		const is = { opprettFraIdenter: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { identtype: 'FNR' } },
				},
			})
			useIdenttypeSync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.identtype')).toBe('FNR')
	})
})

describe('useId2032Sync', () => {
	it('should sync id2032 from context to form', async () => {
		const context = createMockContext({ id2032: true })
		const is = { nyBestilling: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { id2032: false } },
				},
			})
			useId2032Sync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		await waitFor(() => {
			const id2032 = result.current.getValues('pdldata.opprettNyPerson.id2032')
			expect(id2032).toBe(true)
		})
	})

	it('should update form when context id2032 changes', async () => {
		const is = { nyBestilling: true }
		const erOrganisasjon = false

		const { result, rerender } = renderHook(
			({ ctx }) => {
				const formMethods = useForm({
					defaultValues: {
						pdldata: { opprettNyPerson: { id2032: false } },
					},
				})
				useId2032Sync({ context: ctx, formMethods, erOrganisasjon, is })
				return formMethods
			},
			{ initialProps: { ctx: createMockContext({ id2032: false }) } },
		)

		expect(result.current.getValues('pdldata.opprettNyPerson.id2032')).toBe(false)

		rerender({ ctx: createMockContext({ id2032: true }) })

		await waitFor(() => {
			expect(result.current.getValues('pdldata.opprettNyPerson.id2032')).toBe(true)
		})
	})

	it('should not sync when erOrganisasjon is true', async () => {
		const context = createMockContext({ id2032: true })
		const is = { nyBestilling: true }
		const erOrganisasjon = true

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { id2032: false } },
				},
			})
			useId2032Sync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.id2032')).toBe(false)
	})

	it('should not sync when is.leggTil is true', async () => {
		const context = createMockContext({ id2032: true })
		const is = { leggTil: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { id2032: false } },
				},
			})
			useId2032Sync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.id2032')).toBe(false)
	})

	it('should not sync when is.importTestnorge is true', async () => {
		const context = createMockContext({ id2032: true })
		const is = { importTestnorge: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { id2032: false } },
				},
			})
			useId2032Sync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.id2032')).toBe(false)
	})

	it('should handle id2032 as undefined in context gracefully', async () => {
		const context = createMockContext({ id2032: undefined })
		const is = { nyBestilling: true }
		const erOrganisasjon = false

		const { result } = renderHook(() => {
			const formMethods = useForm({
				defaultValues: {
					pdldata: { opprettNyPerson: { id2032: false } },
				},
			})
			useId2032Sync({ context, formMethods, erOrganisasjon, is })
			return formMethods
		})

		expect(result.current.getValues('pdldata.opprettNyPerson.id2032')).toBe(false)
	})
})
