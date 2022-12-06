import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '@/config/kodeverk'

type MaritimtArbeidsforhold = {
	path: string
	onChangeLenket: (fieldPath: string) => any
	disabled?: boolean
	isClearable?: boolean
}

export const MaritimtArbeidsforholdForm = ({
	path,
	onChangeLenket,
	...props
}: MaritimtArbeidsforhold) => {
	return (
		<div>
			<h3>Fartøy</h3>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${path}.skipsregister`}
					label="Skipsregister"
					kodeverk={ArbeidKodeverk.Skipsregistre}
					onChange={onChangeLenket('fartoy[0].skipsregister')}
					isClearable={false}
					{...props}
				/>
				<FormikSelect
					name={`${path}.skipstype`}
					label="Fartøystype"
					kodeverk={ArbeidKodeverk.Skipstyper}
					onChange={onChangeLenket('fartoy[0].skipstype')}
					isClearable={false}
					{...props}
				/>
				<FormikSelect
					name={`${path}.fartsomraade`}
					label="Fartsområde"
					kodeverk={ArbeidKodeverk.Fartsomraader}
					onChange={onChangeLenket('fartoy[0].fartsomraade')}
					isClearable={false}
					{...props}
				/>
			</div>
		</div>
	)
}
