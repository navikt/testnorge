import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Krrstub = ({ formikProps }) => {
	return (
		<Panel heading="Kontakt- og reservasjonsregisteret" hasErrors={panelError(formikProps)}>
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
