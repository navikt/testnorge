import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { addDays, addHours, addWeeks } from 'date-fns'

const initialValuesAktivitet = {
	fom: new Date(),
	tom: addWeeks(new Date(), 2),
}

export const NySykemelding = () => {
	return (
		<div className="flexbox--wrap">
			<FormDollyFieldArray
				name="sykemelding.nySykemelding.aktivitet"
				header="Periode"
				hjelpetekst={
					'Det kan ikke være dager uten sykemelding mellom periodene. ' +
					'Unntaket er når en t.o.m. er på fredag, lørdag og søndag, så kan neste f.o.m. være senest mandag.'
				}
				newEntry={initialValuesAktivitet}
				handleNewEntry={(append: (value: any) => void, values: any[]) => {
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
						fom: addDays(lastEntryTom, 1),
						tom: addWeeks(lastEntryTom, 2),
					}
					append(newAktivitet)
				}}
			>
				{(path: string) => (
					<>
						<FormDatepicker name={`${path}.fom`} label="F.o.m. dato" />
						<FormDatepicker name={`${path}.tom`} label="T.o.m. dato" />
					</>
				)}
			</FormDollyFieldArray>
		</div>
	)
}
