import React, { useContext, useEffect, useState } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikProps } from 'formik'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { DollyCheckbox, FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { initialDeltBosted } from '@/components/fagsystem/pdlf/form/initialValues'
import { DeltBosted } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/DeltBosted'
import * as _ from 'lodash-es'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

interface BarnRelasjonValues {
	formikBag: FormikProps<{}>
	path: string
}

export const BarnRelasjon = ({ formMethods, path }: BarnRelasjonValues) => {
	const opts = useContext(BestillingsveilederContext)
	const erRedigering = !path?.includes('pdldata')

	const [deltBosted, setDeltBosted] = useState(
		_.get(formMethods.getValues(), `${path}.deltBosted`) !== null,
	)

	useEffect(() => {
		const currentValues = _.get(formMethods.getValues(), `${path}.deltBosted`)
		if (deltBosted && currentValues === null) {
			formMethods.setValue(`${path}.deltBosted`, initialDeltBosted)
		} else if (!deltBosted) {
			formMethods.setValue(`${path}.deltBosted`, null)
		}
	}, [deltBosted])

	return (
		<>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${path}.minRolleForPerson`}
					label="Forelders rolle for barn"
					options={Options('foreldreTypePDL')}
					isClearable={false}
				/>
				<FormikCheckbox
					name={`${path}.partnerErIkkeForelder`}
					label="Partner ikke forelder"
					id={`${path}.partnerErIkkeForelder`}
					checkboxMargin
				/>
				{!erRedigering && (
					<DollyCheckbox
						label="Har delt bosted"
						id={`${path}.deltBosted`}
						checked={deltBosted}
						checkboxMargin
						onChange={() => setDeltBosted(!deltBosted)}
						size="small"
						isDisabled={opts?.identtype === 'NPID'}
						title={
							opts?.identtype === 'NPID' ? 'Ikke tilgjengelig for personer med identtype NPID' : ''
						}
					/>
				)}
			</div>
			{deltBosted && !erRedigering && (
				<DeltBosted formMethods={formMethods} path={`${path}.deltBosted`} />
			)}
		</>
	)
}
