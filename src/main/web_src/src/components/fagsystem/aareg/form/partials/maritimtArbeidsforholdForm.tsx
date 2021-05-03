import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '~/config/kodeverk'

type MaritimtArbeidsforhold = {
	path: string
}

export const MaritimtArbeidsforholdForm = ({ path }: MaritimtArbeidsforhold) => {
	return (
		<div>
			<h3>Fartøy</h3>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${path}.skipsregister`}
					label="Skipsregister"
					kodeverk={ArbeidKodeverk.Skipsregistre}
				/>
				<FormikSelect
					name={`${path}.fartoystype`}
					label="Fartøystype"
					kodeverk={ArbeidKodeverk.Skipstyper}
				/>
				<FormikSelect
					name={`${path}.fartsomraade`}
					label="Fartsområde"
					kodeverk={ArbeidKodeverk.Fartsområder}
				/>
			</div>
		</div>
	)
}
