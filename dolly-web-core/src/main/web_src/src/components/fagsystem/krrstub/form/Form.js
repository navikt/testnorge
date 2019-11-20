import React from 'react'
import * as Yup from 'yup'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'

export const KrrstubForm = ({ formikBag }) => {
	return (
		<Panel heading="Kontakt- og reservasjonsregisteret" hasErrors={panelError(formikBag)}>
			<FormikTextInput name="krrstub.epost" label="E-post" />
			<FormikTextInput name="krrstub.mobil" label="Mobilnummer" />
			<FormikSelect
				name="krrstub.registrert"
				label="Registrert i DKIF"
				options={Options('boolean')}
			/>
			<FormikSelect name="krrstub.reservert" label="Reservert" options={Options('boolean')} />
			<FormikDatepicker name="krrstub.gyldigFra" label="Reservasjon gyldig fra" />
		</Panel>
	)
}

KrrstubForm.initialValues = attrs => {
	const initial = {
		krrstub: {
			epost: '',
			gyldigFra: new Date(),
			mobil: '',
			registrert: true,
			reservert: ''
		}
	}
	return attrs.kontaktinformasjon ? initial : {}
}

KrrstubForm.validation = {
	krrstub: Yup.object({
		epost: '',
		gyldigFra: Yup.string().typeError('Formatet må være DD.MM.YYYY.'),
		mobil: Yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
		registrert: '',
		reservert: Yup.string().required('Vennligst velg')
	})
}
