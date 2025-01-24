import * as _ from 'lodash-es'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import { initialSammendragVerdi } from '@/components/fagsystem/arbeidsplassen/form/initialValues'

export const SammendragForm = ({ formMethods }: any) => {
	const sammendragPath = 'arbeidsplassenCV.sammendrag'

	return (
		<Vis attributt={sammendragPath}>
			<h3>Sammendrag</h3>
			<Fritekstfelt
				label="Oppsummering"
				placeholder="Kort oppsummering av kompetanse og personlige egenskaper"
				defaultValue={_.get(formMethods.getValues(), sammendragPath)}
				onBlur={(sammendrag) => formMethods.setValue(sammendragPath, sammendrag?.target?.value)}
				size="small"
				error={_.get(formMethods.getValues(), sammendragPath) === '' ? 'Feltet er påkrevd' : null}
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
