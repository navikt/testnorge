import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export const SyntSykemelding = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDatepicker name="sykemelding.syntSykemelding.startDato" label="Startdato" />
			<FormTextInput
				name="sykemelding.syntSykemelding.arbeidsforholdId"
				label="Arbeidsforhold-ID"
				type="number"
			/>
			<div style={{ marginLeft: '-20px', paddingTop: '27px' }}>
				<Hjelpetekst>
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
				isClearable={true}
			/>
		</div>
	)
}
