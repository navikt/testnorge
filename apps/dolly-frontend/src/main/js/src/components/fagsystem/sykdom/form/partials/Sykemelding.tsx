import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React, { useEffect } from 'react'
import { addDays, addHours, addWeeks } from 'date-fns'
import { useFormContext, useWatch } from 'react-hook-form'
import { sykmeldingTypeOptions } from '@/components/fagsystem/sykdom/SykemeldingTypes'

const basePath = 'sykemelding.nySykemelding'

const initialValuesAktivitet = {
	grad: null as unknown as number,
	reisetilskudd: false,
	fom: new Date(),
	tom: addWeeks(new Date(), 2),
}

const AktivitetFields = ({ path, isVanlig }: { path: string; isVanlig: boolean }) => {
	const { setValue } = useFormContext()
	const gradValue = useWatch({ name: `${path}.grad` })
	const hasGrad = gradValue !== null && gradValue !== undefined && gradValue !== ''

	useEffect(() => {
		if (!hasGrad) {
			setValue(`${path}.reisetilskudd`, false, { shouldDirty: true })
		}
	}, [hasGrad])

	return (
		<>
			{isVanlig && <FormTextInput name={`${path}.grad`} label="Grad (%)" type="number" />}
			<FormDatepicker name={`${path}.fom`} label="F.o.m. dato" />
			<FormDatepicker name={`${path}.tom`} label="T.o.m. dato" />
			{isVanlig && (
				<div style={{ flexBasis: '100%' }}>
					<FormCheckbox
						name={`${path}.reisetilskudd`}
						label="Reisetilskudd"
						disabled={!hasGrad}
						title={!hasGrad ? 'Reisetilskudd krever at grad er satt' : undefined}
					/>
				</div>
			)}
		</>
	)
}

export const Sykemelding = () => {
	const { setValue, getValues } = useFormContext()
	const type = useWatch({ name: `${basePath}.type` })
	const isVanlig = type === 'VANLIG'

	useEffect(() => {
		if (!isVanlig) {
			const aktivitet = getValues(`${basePath}.aktivitet`) || []
			if (aktivitet.length > 1) {
				setValue(`${basePath}.aktivitet`, [aktivitet[0]], { shouldDirty: true })
			}
			const first = getValues(`${basePath}.aktivitet.0`)
			if (first) {
				if (first.grad !== null && first.grad !== undefined && first.grad !== '') {
					setValue(`${basePath}.aktivitet.0.grad`, null, { shouldDirty: true })
				}
				if (first.reisetilskudd) {
					setValue(`${basePath}.aktivitet.0.reisetilskudd`, false, { shouldDirty: true })
				}
			}
		}
	}, [type])

	return (
		<div className="flexbox--wrap">
			<FormSelect
				name={`${basePath}.type`}
				label="Type sykemelding"
				options={sykmeldingTypeOptions}
				isClearable={false}
			/>
			<FormDollyFieldArray
				name={`${basePath}.aktivitet`}
				header="Periode"
				hjelpetekst={
					isVanlig
						? 'Det kan ikke være dager uten sykemelding mellom periodene. ' +
							'Unntaket er når en t.o.m. er på fredag, lørdag og søndag, så kan neste f.o.m. være senest mandag.'
						: undefined
				}
				newEntry={initialValuesAktivitet}
				maxEntries={isVanlig ? null : 1}
				handleNewEntry={
					isVanlig
						? (append: (value: any) => void, values: any[]) => {
								const aktiviteter = values || []
								let lastEntryTom: Date = aktiviteter[aktiviteter.length - 1]?.tom
									? new Date(aktiviteter[aktiviteter.length - 1].tom)
									: new Date()
								if (lastEntryTom.getDay() === 5 || lastEntryTom.getDay() === 6) {
									lastEntryTom = addDays(lastEntryTom, lastEntryTom.getDay() === 5 ? 2 : 1)
								}
								if (lastEntryTom.getHours() === 0) {
									lastEntryTom = addHours(lastEntryTom, 2)
								}
								const newAktivitet = {
									grad: null as unknown as number,
									reisetilskudd: false,
									fom: addDays(lastEntryTom, 1),
									tom: addWeeks(lastEntryTom, 2),
								}
								append(newAktivitet)
							}
						: undefined
				}
			>
				{(path: string) => <AktivitetFields path={path} isVanlig={isVanlig} />}
			</FormDollyFieldArray>
		</div>
	)
}
