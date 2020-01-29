import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Diskresjonskoder } from './diskresjonskoder/Diskresjonskoder'

export const Diverse = ({ formikBag }) => (
	<React.Fragment>
		<FormikSelect name="tpsf.kjonn" label="Kjønn" kodeverk="Kjønnstyper" visHvisAvhuket />

		<FormikSelect
			name="tpsf.harMellomnavn"
			label="Mellomnavn"
			options={Options('boolean')}
			visHvisAvhuket
		/>

		<FormikSelect
			name="tpsf.sprakKode"
			label="Språk"
			kodeverk="Språk"
			size="large"
			visHvisAvhuket
		/>

		<FormikDatepicker name="tpsf.egenAnsattDatoFom" label="Egenansatt fra" visHvisAvhuket />

		<Vis attributt="tpsf.erForsvunnet">
			<FormikSelect name="tpsf.erForsvunnet" label="Er forsvunnet" options={Options('boolean')} />

			<FormikDatepicker
				name="tpsf.forsvunnetDato"
				label="Forsvunnet dato"
				disabled={!formikBag.values.tpsf.erForsvunnet}
				fastfield={false}
			/>
		</Vis>
		<Diskresjonskoder basePath="tpsf" formikBag={formikBag} />
	</React.Fragment>
)
