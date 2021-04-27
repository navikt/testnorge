import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '~/config/kodeverk'

export const ForenkletOppgjoersordningForm = ({ formikBag }) => {
	return (
		<>
			<FormikDatepicker
				name={'aareg[0].arbeidsforhold[0].ansettelsesPeriode.fom'}
				label="Ansatt fra"
			/>
			<FormikDatepicker
				name={'aareg[0].arbeidsforhold[0].ansettelsesPeriode.tom'}
				label="Ansatt til"
			/>
			<FormikSelect
				name={'aareg[0].arbeidsforhold[0].arbeidsavtale.yrke'}
				label="Yrke"
				kodeverk={ArbeidKodeverk.Yrker}
				size="xxlarge"
				isClearable={false}
				optionHeight={50}
			/>
		</>
	)
}
