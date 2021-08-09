import React, { ReactFragment } from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '~/config/kodeverk'
import EgenOrganisasjonConnector from '~/components/organisasjonSelect/EgenOrganisasjonConnector'
import { FormikProps } from 'formik'
import { AaregListe } from '~/components/fagsystem/aareg/AaregTypes'

interface ForenkletOppgjoersordningForm {
	formikBag: FormikProps<{ aareg: AaregListe }>
}

export const ForenkletOppgjoersordningForm = ({
	formikBag
}: ForenkletOppgjoersordningForm): ReactFragment => {
	return (
		<>
			<EgenOrganisasjonConnector
				name={'aareg[0].arbeidsforhold[0].arbeidsgiver.orgnummer'}
				isClearable={false}
				onChange={(selected: any) =>
					formikBag.setFieldValue(
						'aareg[0].arbeidsforhold[0].arbeidsgiver.orgnummer',
						selected.value
					)
				}
			/>
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
