import React, { useContext, useEffect } from 'react'
import { FormProvider, useFormContext } from 'react-hook-form'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Option } from '@/service/SelectOptionsOppslag'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export function NyIdent() {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const identtypePath = 'pdldata.opprettNyPerson.identtype'
	const id2032Path = 'pdldata.opprettNyPerson.id2032'
	const formMethods = useFormContext()
	const identtype = formMethods.watch(identtypePath)

	useEffect(() => {
		const ctxIdenttype = opts.initialValues?.pdldata?.opprettNyPerson?.identtype
		if (ctxIdenttype) {
			formMethods.setValue(identtypePath, ctxIdenttype)
		}
		const ctxId2032 = opts.initialValues?.pdldata?.opprettNyPerson?.id2032
		if (typeof ctxId2032 === 'boolean') {
			formMethods.setValue(id2032Path, ctxId2032)
		}
	}, [opts.initialValues])

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
							const parsedValue = selectedValue ? parseInt(selectedValue, 10) : undefined
							if (parsedValue) {
								opts.updateContext && opts.updateContext({ antall: parsedValue })
								formMethods.setValue('antall', parsedValue)
							}
						}}
					/>
					<FormSelect
						name={identtypePath}
						label="Velg identtype"
						size="medium"
						onChange={(option: Option) => {
							formMethods.setValue(identtypePath, option.value)
							opts.updateContext && opts.updateContext({ identtype: option.value })
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
								opts.updateContext && opts.updateContext({ id2032: val })
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
