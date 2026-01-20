import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ArbeidKodeverk } from '@/config/kodeverk'

type ArbeidsavtaleProps = {
	path: string
	onChangeLenket: Function
	values: any
}

export const ArbeidsavtaleForm = ({
	path,
	onChangeLenket,
	values,
	...props
}: ArbeidsavtaleProps) => (
	<div>
		<h3>Ansettelsesdetaljer</h3>
		<div className="flexbox--flex-wrap">
			<FormSelect
				name={`${path}.yrke`}
				label="Yrke"
				kodeverk={ArbeidKodeverk.Yrker}
				size="xxxlarge"
				isClearable={false}
				optionHeight={50}
				onChange={onChangeLenket('arbeidsavtale.yrke')}
				{...props}
			/>
			<FormSelect
				name={`${path}.ansettelsesform`}
				label="Ansettelsesform"
				kodeverk={ArbeidKodeverk.AnsettelsesformAareg}
				onChange={onChangeLenket('arbeidsavtale.ansettelsesform')}
				isClearable={false}
				{...props}
			/>
			<FormTextInput
				key={`${path}.stillingsprosent`}
				name={`${path}.stillingsprosent`}
				label="Stillingsprosent"
				type="number"
				onBlur={onChangeLenket('arbeidsavtale.stillingsprosent')}
				{...props}
			/>
			<FormDatepicker
				name={`${path}.endringsdatoStillingsprosent`}
				label="Endringsdato stillingsprosent"
				onChange={onChangeLenket('arbeidsavtale.endringsdatoStillingsprosent')}
				{...props}
			/>
			<FormDatepicker
				name={`${path}.sisteLoennsendringsdato`}
				label="Endringsdato lønn"
				onChange={onChangeLenket('arbeidsavtale.sisteLoennsendringsdato')}
				{...props}
			/>
			<FormSelect
				name={`${path}.arbeidstidsordning`}
				label="Arbeidstidsordning"
				kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
				size="xxlarge"
				isClearable={false}
				onChange={onChangeLenket('arbeidsavtale.arbeidstidsordning')}
				{...props}
			/>
			<FormTextInput
				key={`${path}.avtaltArbeidstimerPerUke`}
				name={`${path}.avtaltArbeidstimerPerUke`}
				label="Avtalte arbeidstimer per uke"
				type="number"
				onBlur={onChangeLenket('arbeidsavtale.avtaltArbeidstimerPerUke')}
				{...props}
			/>
		</div>
	</div>
)
