import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '~/config/kodeverk'

type MaritimtArbeidsforhold = {
	path: string
	onChangeLenket: Function
}

export const MaritimtArbeidsforholdForm = ({ path, onChangeLenket }: MaritimtArbeidsforhold) => {
	return (
		<div>
			<h3>Fartøy</h3>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${path}.skipsregister`}
					label="Skipsregister"
					kodeverk={ArbeidKodeverk.Skipsregistre}
					onChange={onChangeLenket('fartoy.skipsregister')}
				/>
				<FormikSelect
					name={`${path}.fartoystype`}
					label="Fartøystype"
					kodeverk={ArbeidKodeverk.Skipstyper}
					onChange={onChangeLenket('fartoy.fartoystype')}
				/>
				<FormikSelect
					name={`${path}.fartsomraade`}
					label="Fartsområde"
					kodeverk={ArbeidKodeverk.Fartsområder}
					onChange={onChangeLenket('fartoy.fartsomraade')}
				/>
			</div>
		</div>
	)
}
