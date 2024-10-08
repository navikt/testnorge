import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SigrunKodeverk } from '@/config/kodeverk'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
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
		<FormDollyFieldArray
			maxEntries={1}
			name={path}
			header={header}
			newEntry={initialGrunnlag}
			nested
		>
			{(path, idx) => {
				const typeInntekt = formMethods.watch(`${path}.tekniskNavn`)
				return (
					<div className={'flexbox--space sigrun-form'} key={idx}>
						<FormSelect
							name={`${path}.tekniskNavn`}
							label="Type inntekt"
							options={filteredTekniskNavnOptions || []}
							size="xxlarge"
							isClearable={false}
							optionHeight={50}
						/>
						{typeInntekt === 'skatteoppgjoersdato' ? (
							<FormDatepicker name={`${path}.verdi`} label="Oppgjørsdato" />
						) : (
							<FormTextInput name={`${path}.verdi`} label="Verdi" type="number" />
						)}
					</div>
				)
			}}
		</FormDollyFieldArray>
	)
}
