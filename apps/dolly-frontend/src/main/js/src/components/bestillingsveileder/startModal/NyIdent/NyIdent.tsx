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
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { BVOptions } from '@/components/bestillingsveileder/options/options'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export function NyIdent({ gruppeId }: any) {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { dollyEnvironments } = useDollyEnvironments()
	const identtypePath = 'pdldata.opprettNyPerson.identtype'
	const id2032Path = 'pdldata.opprettNyPerson.id2032'
	const formMethods = useFormContext()
	const [identtype, setIdenttype] = useState(formMethods.watch(identtypePath))

	return (
		<FormProvider {...formMethods}>
			<div style={{ flexDirection: 'column', alignItems: 'start' }}>
				<h3 style={{ marginBottom: 0 }}>Velg antall og type</h3>
				<div className="ny-bestilling-form_selects">
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
					<div className="flexbox--flex-wrap" style={{ alignItems: 'baseline' }}>
						<FormCheckbox
							name={id2032Path}
							label="Ny ident (2032)"
							afterChange={(val: boolean) => {
								opts.id2032 = val
								formMethods.setValue(id2032Path, val)
							}}
							checkboxMargin
						/>
						<Hjelpetekst style={{ marginLeft: -50 }}>
							For å øke tilgangen på gyldige fødselsnumre og d-numre, har Stortinget vedtatt å ta i
							bruk nye nummerserier fra 2032. Alle eksisterende fødselsnumre og d-numre vil
							fremdeles være gyldige, men datasystemene må i tillegg kunne lese de nye
							nummerseriene.
						</Hjelpetekst>
					</div>
				</div>
			</div>
		</FormProvider>
	)
}
