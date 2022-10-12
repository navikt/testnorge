import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SigrunKodeverk } from '~/config/kodeverk'
import _get from 'lodash/get'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const EnkeltinntektForm = ({ path, header, initialGrunnlag, tjeneste, formikBag }) => {
	return (
		<FormikDollyFieldArray name={path} header={header} newEntry={initialGrunnlag} nested>
			{(path, idx) => {
				const typeInntekt = _get(formikBag.values, `${path}.tekniskNavn`)
				return (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.tekniskNavn`}
							label="Type inntekt"
							kodeverk={SigrunKodeverk[tjeneste]}
							size="xxlarge"
							isClearable={false}
							optionHeight={50}
						/>
						{typeInntekt === 'skatteoppgjoersdato' ? (
							<FormikDatepicker name={`${path}.verdi`} label="Oppgjørsdato" fastfield={false} />
						) : (
							<FormikTextInput name={`${path}.verdi`} label="Beløp" type="number" />
						)}
					</React.Fragment>
				)
			}}
		</FormikDollyFieldArray>
	)
}
