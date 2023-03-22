import _get from 'lodash/get'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { initialSammendragVerdi } from '@/components/fagsystem/arbeidsplassen/form/initialValues'

export const SammendragForm = ({ formikBag }) => {
	const sammendragPath = 'arbeidsplassenCV.sammendrag'

	return (
		<Vis attributt={sammendragPath}>
			<h3>Sammendrag</h3>
			<Fritekstfelt
				label="Oppsummering"
				placeholder="Kort oppsummering av kompetanse og personlige egenskaper"
				defaultValue={_get(formikBag.values, sammendragPath)}
				onBlur={(sammendrag) => formikBag.setFieldValue(sammendragPath, sammendrag?.target?.value)}
				size="small"
				key={`sammendrag_${_get(formikBag.values, sammendragPath)}`}
				error={_get(formikBag.values, sammendragPath) === '' ? 'Feltet er påkrevd' : null}
				resize
			/>
			<EraseFillButtons
				formikBag={formikBag}
				path={sammendragPath}
				initialErase={''}
				initialFill={initialSammendragVerdi}
			/>
		</Vis>
	)
}
