import React, { useContext, useState } from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { NyIdent } from '@/components/bestillingsveileder/startModal/NyIdent/NyIdent'
import { EksisterendeIdent } from '@/components/bestillingsveileder/startModal/EksisterendeIdent/EksisterendeIdent'
import { useFormContext } from 'react-hook-form'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { deriveBestillingsveilederState } from '@/components/bestillingsveileder/options/deriveBestillingsveilederState'

export const VelgIdenttype = ({ gruppeId }: any) => {
	const formMethods = useFormContext()
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const harEksisterendeIdenter = formMethods.getValues('opprettFraIdenter')
	const [type, setType] = useState(harEksisterendeIdenter ? 'eksisterende' : 'ny')

	const handleTypeChange = (value: string) => {
		setType(value)
		const environments = formMethods.getValues('environments') || []
		const rawGruppeId = formMethods.getValues('gruppeId') ?? opts.gruppeId ?? null
		const gruppeIdValue: number | undefined =
			typeof rawGruppeId === 'number' ? rawGruppeId : rawGruppeId ? Number(rawGruppeId) : undefined
		if (value === 'eksisterende') {
			const config = {
				gruppeId: gruppeIdValue || undefined,
				antall: null,
				identtype: opts?.identtype || 'FNR',
				id2032: opts?.id2032 || false,
				mal: opts?.mal,
				opprettFraIdenter: [],
			}
			const derived = deriveBestillingsveilederState(config as any, environments)
			formMethods.reset(derived.initialValues)
			formMethods.setValue('gruppeId', gruppeIdValue)
			formMethods.unregister('antall')
			opts.updateContext &&
				opts.updateContext({
					is: { ...opts.is, opprettFraIdenter: true },
					opprettFraIdenter: [],
					antall: null,
				})
		} else {
			const antallNy = (formMethods.getValues('antall') || 1) as number
			const config = {
				gruppeId: gruppeIdValue || undefined,
				antall: antallNy,
				identtype: opts?.identtype || 'FNR',
				id2032: opts?.id2032 || false,
				mal: opts?.mal,
			}
			const derived = deriveBestillingsveilederState(config as any, environments)
			formMethods.reset(derived.initialValues)
			formMethods.setValue('gruppeId', gruppeIdValue)
			formMethods.setValue('antall', antallNy)
			opts.updateContext &&
				opts.updateContext({
					is: { ...opts.is, opprettFraIdenter: false },
					opprettFraIdenter: undefined,
					antall: antallNy,
				})
		}
	}

	return (
		<div className="ny-bestilling-form_input">
			<h2>Opprett personer</h2>
			<ToggleGroup size={'small'} value={type} onChange={(value) => handleTypeChange(value)}>
				<ToggleGroup.Item
					data-testid={TestComponentSelectors.TOGGLE_NY_PERSON}
					value={'ny'}
					key={'ny'}
				>
					Ny person
				</ToggleGroup.Item>
				<ToggleGroup.Item
					data-testid={TestComponentSelectors.TOGGLE_EKSISTERENDE_PERSON}
					value={'eksisterende'}
					key={'eksisterende'}
				>
					Eksisterende person
				</ToggleGroup.Item>
			</ToggleGroup>
			{type === 'ny' && <NyIdent />}
			{type === 'eksisterende' && <EksisterendeIdent gruppeId={gruppeId} />}
		</div>
	)
}
