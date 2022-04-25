import React, { useEffect, useState } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikProps } from 'formik'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { DollyCheckbox, FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { initialDeltBosted } from '~/components/fagsystem/pdlf/form/initialValues'
import { DeltBosted } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/DeltBosted'

interface BarnRelasjonValues {
	formikBag: FormikProps<{}>
	path: string
}

export const BarnRelasjon = ({ formikBag, path }: BarnRelasjonValues) => {
	const [deltBosted, setDeltBosted] = useState(false)

	useEffect(() => {
		const newValue = deltBosted ? initialDeltBosted : null
		formikBag.setFieldValue(`${path}.deltBosted`, newValue)
	}, [deltBosted])

	return (
		<>
			<FormikSelect
				name={`${path}.minRolleForPerson`}
				label="Forelders rolle for barn"
				options={Options('foreldreTypePDL')}
				isClearable={false}
			/>
			<FormikCheckbox
				name={`${path}.partnerErIkkeForelder`}
				label="Partner ikke forelder"
				checkboxMargin
			/>
			<DollyCheckbox
				label="Har delt bosted"
				onChange={() => setDeltBosted(!deltBosted)}
				size="medium"
				checkboxMargin
			/>
			{deltBosted && <DeltBosted formikBag={formikBag} path={`${path}.deltBosted`} />}
		</>
	)
}
