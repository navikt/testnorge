import React, { useEffect, useState } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { DollyCheckbox, FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { initialDeltBosted } from '@/components/fagsystem/pdlf/form/initialValues'
import { DeltBosted } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/DeltBosted'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface BarnRelasjonValues {
	formMethods: UseFormReturn
	path: string
}

export const BarnRelasjon = ({ formMethods, path }: BarnRelasjonValues) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const erRedigering = !path?.includes('pdldata')

	const [deltBosted, setDeltBosted] = useState(formMethods.watch(`${path}.deltBosted`) !== null)

	const hideDeltBostedCheckbox =
		(formMethods.getValues('pdldata.person.deltBosted')?.length > 0 && !deltBosted) || erRedigering

	useEffect(() => {
		const currentValues = formMethods.watch(`${path}.deltBosted`)
		if (deltBosted && currentValues === null) {
			formMethods.setValue(`${path}.deltBosted`, initialDeltBosted)
		} else if (!deltBosted) {
			formMethods.setValue(`${path}.deltBosted`, null)
		}
		formMethods.trigger(path)
	}, [deltBosted])

	const testnorgePerson = opts?.identMaster === 'PDL'
	return (
		<>
			<div className="flexbox--flex-wrap">
				<FormSelect
					name={`${path}.minRolleForPerson`}
					label="Forelders rolle for barn"
					options={Options('foreldreTypePDL')}
					isClearable={false}
				/>
				<FormCheckbox
					name={`${path}.partnerErIkkeForelder`}
					label="Partner ikke forelder"
					id={`${path}.partnerErIkkeForelder`}
					vis={!testnorgePerson}
					checkboxMargin
					wrapperSize="shrink"
				/>
				{!hideDeltBostedCheckbox && (
					<DollyCheckbox
						label="Har delt bosted"
						id={`${path}.deltBosted`}
						checked={deltBosted}
						checkboxMargin
						onChange={() => setDeltBosted(!deltBosted)}
						size="small"
						disabled={opts?.identtype === 'NPID'}
						vis={!testnorgePerson}
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
