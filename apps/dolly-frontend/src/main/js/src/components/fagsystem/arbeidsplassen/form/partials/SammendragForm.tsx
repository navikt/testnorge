import _get from 'lodash/get'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { initialSammendragVerdi } from '@/components/fagsystem/arbeidsplassen/form/initialValues'

export const SammendragForm = ({ formMethods }) => {
	const sammendragPath = 'arbeidsplassenCV.sammendrag'

	return (
		<Vis attributt={sammendragPath}>
			<h3>Sammendrag</h3>
			<Fritekstfelt
				label="Oppsummering"
				placeholder="Kort oppsummering av kompetanse og personlige egenskaper"
				defaultValue={_get(formMethods.getValues(), sammendragPath)}
				onBlur={(sammendrag) => formMethods.setValue(sammendragPath, sammendrag?.target?.value)}
				size="small"
				key={`sammendrag_${_get(formMethods.getValues(), sammendragPath)}`}
				error={_get(formMethods.getValues(), sammendragPath) === '' ? 'Feltet er påkrevd' : null}
				resize
			/>
			<EraseFillButtons
				formMethods={formMethods}
				path={sammendragPath}
				initialErase={''}
				initialFill={initialSammendragVerdi}
			/>
		</Vis>
	)
}
