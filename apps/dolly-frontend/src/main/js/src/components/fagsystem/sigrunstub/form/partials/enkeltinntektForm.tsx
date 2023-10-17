import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SigrunKodeverk } from '@/config/kodeverk'
import * as _ from 'lodash-es'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import React from 'react'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { getYear } from 'date-fns'

export const EnkeltinntektForm = ({
	path,
	header,
	initialGrunnlag,
	tjeneste,
	inntektsaar,
	formikBag,
}) => {
	const { kodeverk: tekniskNavnOptions } = useKodeverk(SigrunKodeverk[tjeneste])

	const getFilteredTekniskNavnOptions = () => {
		return tekniskNavnOptions?.koder?.filter((item) => {
			return getYear(new Date(item?.gyldigFra)) <= inntektsaar
		})
	}
	const filteredTekniskNavnOptions = getFilteredTekniskNavnOptions()

	return (
		<FormikDollyFieldArray name={path} header={header} newEntry={initialGrunnlag} nested>
			{(path, idx) => {
				const typeInntekt = _.get(formikBag.values, `${path}.tekniskNavn`)
				return (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.tekniskNavn`}
							label="Type inntekt"
							options={filteredTekniskNavnOptions || []}
							size="xxlarge"
							isClearable={false}
							optionHeight={50}
						/>
						{typeInntekt === 'skatteoppgjoersdato' ? (
							<FormikDatepicker name={`${path}.verdi`} label="Oppgjørsdato" fastfield={false} />
						) : (
							<FormikTextInput name={`${path}.verdi`} label="Verdi" type="number" />
						)}
					</React.Fragment>
				)
			}}
		</FormikDollyFieldArray>
	)
}
