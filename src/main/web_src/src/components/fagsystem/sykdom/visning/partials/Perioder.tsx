import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

type Perioder = {
	data: Array<Periode>
}

type Periode = {
	fom: string
	tom: string
	aktivitet: Aktivitet
}

type Aktivitet = {
	aktivitet: string
	behandlingsdager: number
	grad: number
	reisetilskudd: boolean
}

export const Perioder = ({ data }: Perioder) => {
	if (!data || data.length === 0) return null
	return (
		<>
			<h4>Perioder</h4>
			<DollyFieldArray data={data} nested>
				{(periode: Periode, idx: string) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="F.o.m. dato" value={Formatters.formatStringDates(periode.fom)} />
						<TitleValue title="T.o.m. dato" value={Formatters.formatStringDates(periode.tom)} />
						<TitleValue title="Aktivitet" value={periode.aktivitet.aktivitet} />
						<TitleValue
							title="Antall behandlingsdager"
							value={periode.aktivitet.behandlingsdager}
						/>
						<TitleValue title="Grad" value={periode.aktivitet.grad} />
						<TitleValue
							title="Har reisetilskudd"
							value={Formatters.oversettBoolean(periode.aktivitet.reisetilskudd)}
						/>
					</div>
				)}
			</DollyFieldArray>
		</>
	)
}
