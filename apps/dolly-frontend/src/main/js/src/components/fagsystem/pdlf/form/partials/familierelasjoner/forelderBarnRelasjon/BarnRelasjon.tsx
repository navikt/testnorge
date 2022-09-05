import React, { useEffect, useState } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikProps } from 'formik'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { DollyCheckbox, FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { initialDeltBosted } from '~/components/fagsystem/pdlf/form/initialValues'
import { DeltBosted } from '~/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/DeltBosted'
import _get from 'lodash/get'

interface BarnRelasjonValues {
	formikBag: FormikProps<{}>
	path: string
}

export const BarnRelasjon = ({ formikBag, path }: BarnRelasjonValues) => {
	const [deltBosted, setDeltBosted] = useState(
		_get(formikBag.values, `${path}.deltBosted`) !== null
	)

	useEffect(() => {
		const currentValues = _get(formikBag.values, `${path}.deltBosted`)
		if (deltBosted && currentValues === null) {
			formikBag.setFieldValue(`${path}.deltBosted`, initialDeltBosted)
		} else if (!deltBosted) {
			formikBag.setFieldValue(`${path}.deltBosted`, null)
		}
	}, [deltBosted])

	return (
		<>
			<FormikSelect
				name={`${path}.minRolleForPerson`}
				label="Forelders rolle for barn"
				options={Options('foreldreTypePDL')}
				isClearable={false}
			/>
			<FormikCheckbox name={`${path}.partnerErIkkeForelder`} label="Partner ikke forelder" />
			<DollyCheckbox
				label="Har delt bosted"
				checked={deltBosted}
				onChange={() => setDeltBosted(!deltBosted)}
				size="small"
			/>
			{deltBosted && <DeltBosted formikBag={formikBag} path={`${path}.deltBosted`} />}
		</>
	)
}
