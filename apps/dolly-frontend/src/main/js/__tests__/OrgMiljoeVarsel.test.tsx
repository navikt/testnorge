import React from 'react'
import { describe, expect, it, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { FormProvider, useForm } from 'react-hook-form'
import { OrgMiljoeVarsel } from '@/components/miljoVelger/OrgMiljoeVarsel'
import { SWRConfig } from 'swr'

const mockFetcher = vi.fn()

vi.mock('@/api', () => ({
	fetcher: (...args: any[]) => mockFetcher(...args),
	multiFetcherAareg: vi.fn(),
}))

const Wrapper = ({
	children,
	defaultValues,
}: {
	children: React.ReactNode
	defaultValues: Record<string, any>
}) => {
	const methods = useForm({ defaultValues })
	return (
		<SWRConfig value={{ provider: () => new Map(), dedupingInterval: 0 }}>
			<FormProvider {...methods}>{children}</FormProvider>
		</SWRConfig>
	)
}

describe('OrgMiljoeVarsel', () => {
	beforeEach(() => {
		vi.clearAllMocks()
	})

	it('shouldShowWarningWhenOrgNotInSelectedEnvironment', async () => {
		const orgData = {
			organisasjonsnummer: '123456789',
			organisasjonsnavn: 'Test AS',
			enhetstype: 'BEDR',
		}
		mockFetcher.mockResolvedValue([{ q1: orgData }])

		render(
			<Wrapper
				defaultValues={{
					aareg: [{ arbeidsgiver: { orgnummer: '123456789' } }],
					environments: ['q1', 'q2'],
				}}
			>
				<OrgMiljoeVarsel />
			</Wrapper>,
		)

		const warning = await screen.findByText(
			/Valgt organisasjon 123456789 ikke funnet i Q2.*det miljøet/,
		)
		expect(warning).toBeInTheDocument()
	})

	it('shouldCombineMultipleMissingEnvsInOneWarning', async () => {
		const orgData = {
			organisasjonsnummer: '123456789',
			organisasjonsnavn: 'Test AS',
			enhetstype: 'BEDR',
		}
		mockFetcher.mockResolvedValue([{ q1: orgData }])

		render(
			<Wrapper
				defaultValues={{
					aareg: [{ arbeidsgiver: { orgnummer: '123456789' } }],
					environments: ['q1', 'q2', 'q4'],
				}}
			>
				<OrgMiljoeVarsel />
			</Wrapper>,
		)

		const warning = await screen.findByText(
			/Valgt organisasjon 123456789 ikke funnet i Q2, Q4.*de miljøene/,
		)
		expect(warning).toBeInTheDocument()
	})

	it('shouldNotShowWarningWhenOrgExistsInAllSelectedEnvironments', async () => {
		const orgData = {
			organisasjonsnummer: '123456789',
			organisasjonsnavn: 'Test AS',
			enhetstype: 'BEDR',
		}
		mockFetcher.mockResolvedValue([{ q1: orgData }, { q2: orgData }])

		render(
			<Wrapper
				defaultValues={{
					aareg: [{ arbeidsgiver: { orgnummer: '123456789' } }],
					environments: ['q1', 'q2'],
				}}
			>
				<OrgMiljoeVarsel />
			</Wrapper>,
		)

		await new Promise((r) => setTimeout(r, 200))
		expect(screen.queryByText(/ikke funnet i/)).not.toBeInTheDocument()
	})

	it('shouldNotRenderWhenNoAaregOrgs', async () => {
		render(
			<Wrapper defaultValues={{ aareg: [], environments: ['q1'] }}>
				<OrgMiljoeVarsel />
			</Wrapper>,
		)

		await new Promise((r) => setTimeout(r, 100))
		expect(screen.queryByText(/ikke funnet i/)).not.toBeInTheDocument()
	})

	it('shouldNotRenderWhenNoEnvironmentsSelected', async () => {
		render(
			<Wrapper
				defaultValues={{
					aareg: [{ arbeidsgiver: { orgnummer: '123456789' } }],
					environments: [],
				}}
			>
				<OrgMiljoeVarsel />
			</Wrapper>,
		)

		await new Promise((r) => setTimeout(r, 100))
		expect(screen.queryByText(/ikke funnet i/)).not.toBeInTheDocument()
	})
})
