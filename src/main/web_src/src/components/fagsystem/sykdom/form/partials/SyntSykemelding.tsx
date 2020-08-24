import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'

export const SyntSykemelding = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDatepicker name="sykemelding.syntSykemelding.startDato" label="Startdato" />
			<OrganisasjonMedArbeidsforholdSelect
				path="sykemelding.syntSykemelding.orgnummer"
				label="Organisasjonsnummer"
			/>
			<FormikTextInput
				name="sykemelding.syntSykemelding.arbeidsforholdId"
				label="Arbeidsforhold-ID"
				type="number"
			/>
		</div>
	)
}
