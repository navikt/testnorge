import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SigrunKodeverk } from '@/config/kodeverk'
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
	formMethods,
}) => {
	const { kodeverk: tekniskNavnOptions } = useKodeverk(SigrunKodeverk[tjeneste])

	const getFilteredTekniskNavnOptions = () => {
		return tekniskNavnOptions?.filter((item) => {
			return getYear(new Date(item?.gyldigFra)) <= inntektsaar
		})
	}
	const filteredTekniskNavnOptions = getFilteredTekniskNavnOptions()

	return (
		<FormikDollyFieldArray name={path} header={header} newEntry={initialGrunnlag} nested>
			{(path, idx) => {
				const typeInntekt = formMethods.watch(`${path}.tekniskNavn`)
				return (
					<div className={'flexbox--space sigrun-form'} key={idx}>
						<FormikSelect
							name={`${path}.tekniskNavn`}
							label="Type inntekt"
							options={filteredTekniskNavnOptions || []}
							size="xxlarge"
							isClearable={false}
							optionHeight={50}
						/>
						{typeInntekt === 'skatteoppgjoersdato' ? (
							<FormikDatepicker name={`${path}.verdi`} label="Oppgjørsdato" />
						) : (
							<FormikTextInput name={`${path}.verdi`} label="Verdi" type="number" />
						)}
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
