import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import Hjelpetekst from '~/components/hjelpetekst'

export const SyntSykemelding = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDatepicker name="sykemelding.syntSykemelding.startDato" label="Startdato" />
			<FormikTextInput
				name="sykemelding.syntSykemelding.arbeidsforholdId"
				label="Arbeidsforhold-ID"
				type="number"
			/>
			<div style={{ marginLeft: '-20px', paddingTop: '27px' }}>
				<Hjelpetekst hjelpetekstFor="Arbeidsforhold-ID">
					Nummeret på arbeidsforholdet du ønsker å knytte sykemeldingen til. Når du har opprettet
					arbeidsforhold i bestillingen ser du disse som en nummerert liste i
					Arbeidsforhold-panelet. <br />
					<br />
					La feltet være blankt for å bruke første gyldige arbeidsforhold
				</Hjelpetekst>
			</div>
			<OrganisasjonMedArbeidsforholdSelect
				path="sykemelding.syntSykemelding.orgnummer"
				label="Organisasjonsnummer"
			/>
		</div>
	)
}
