import React, { useContext, useState } from 'react'
import { FormProvider, useFormContext } from 'react-hook-form'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Option } from '@/service/SelectOptionsOppslag'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

export function NyIdent() {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
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
							setIdenttype(option.value)
							formMethods.setValue(identtypePath, option.value)
						}}
						value={identtype}
						options={Options('identtype')}
						isClearable={false}
					/>
					<FormTextInput
						name="antall"
						label="Antall"
						type="number"
						size="medium"
						onBlur={(event) => {
							const selectedValue = event?.target?.value || '1'
							opts.antall = selectedValue
							formMethods.setValue('antall', selectedValue)
						}}
					/>
				</div>
			</div>
		</FormProvider>
	)
}
