import React, { useContext, useState } from 'react'
import { FormProvider, useFormContext } from 'react-hook-form'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Option } from '@/service/SelectOptionsOppslag'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { BVOptions } from '@/components/bestillingsveileder/options/options'

export function NyIdent({ gruppeId }: any) {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { dollyEnvironments } = useDollyEnvironments()
	const identtypePath = 'pdldata.opprettNyPerson.identtype'
	const formMethods = useFormContext()
	const [identtype, setIdenttype] = useState(formMethods.watch(identtypePath))

	return (
		<FormProvider {...formMethods}>
			<div style={{ flexDirection: 'column', alignItems: 'start' }}>
				<h3 style={{ marginBottom: 0 }}>Velg type og antall</h3>
				<div className="ny-bestilling-form_selects">
					<FormSelect
						name={identtypePath}
						label="Velg identtype"
						size="medium"
						onChange={(option: Option) => {
							opts.identtype = option.value
							opts.mal = undefined
							const options = BVOptions(opts, gruppeId, dollyEnvironments)
							setIdenttype(option.value)
							formMethods.reset(options.initialValues)
							formMethods.setValue('gruppeId', gruppeId)
						}}
						value={identtype}
						options={Options('identtype')}
						isClearable={false}
					/>
					<FormTextInput
						useControlled
						name="antall"
						label="Antall"
						type="number"
						size="medium"
						onBlur={(event) => {
							const selectedValue = event?.target?.value
							opts.antall = selectedValue
							formMethods.setValue('antall', selectedValue)
						}}
					/>
				</div>
			</div>
		</FormProvider>
	)
}
