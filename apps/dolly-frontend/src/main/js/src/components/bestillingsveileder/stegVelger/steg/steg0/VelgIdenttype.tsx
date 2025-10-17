import React, { useState } from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { NyIdent } from '@/components/bestillingsveileder/startModal/NyIdent/NyIdent'
import { EksisterendeIdent } from '@/components/bestillingsveileder/startModal/EksisterendeIdent/EksisterendeIdent'
import { useFormContext } from 'react-hook-form'

export const VelgIdenttype = ({ gruppeId }: any) => {
	const formMethods = useFormContext()
	const harEksisterendeIdenter = formMethods.getValues('opprettFraIdenter')
	const [type, setType] = useState(harEksisterendeIdenter ? 'eksisterende' : 'ny')

	return (
		<div className="ny-bestilling-form_input">
			<h2>Opprett personer</h2>
			<ToggleGroup size={'small'} value={type} onChange={(value) => setType(value)}>
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
			{type === 'ny' && <NyIdent gruppeId={gruppeId} />}
			{type === 'eksisterende' && <EksisterendeIdent gruppeId={gruppeId} />}
		</div>
	)
}
