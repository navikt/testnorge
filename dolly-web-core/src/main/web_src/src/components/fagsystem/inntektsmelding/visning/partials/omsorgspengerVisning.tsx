import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

interface OmsorgspengerVisning {
	data: Array<Omsorgspenger>
}

type Omsorgspenger = {
	delvisFravaersListe?: DelvisFravaersListe
	fravaersPerioder?: string
	harUtbetaltPliktigeDager?: boolean
}

type DelvisFravaersListe = {
	dato?: string
	timer?: number
}

type FravaersPerioder = {
	fom?: string
	tom?: string
}

export default ({ data }: OmsorgspengerVisning) => {
	if (!data || data.length < 1) return null

	return (
		<>
			<h4>Omsorgspenger</h4>
			<DollyFieldArray data={data}>
				{(id: Omsorgspenger) => (
					<>
						<TitleValue title="Har utbetalt pliktige dager" value={id.harUtbetaltPliktigeDager} />
						<DollyFieldArray data={id.delvisFravaersListe} header="Delvis fravær">
							{(id: DelvisFravaersListe) => (
								<>
									<div className="person-visning_content">
										<TitleValue title="Dato" value={id.dato} />
										<TitleValue title="Timer" value={id.timer} />
									</div>
								</>
							)}
						</DollyFieldArray>

						<DollyFieldArray data={id.fravaersPerioder} header="Delvis fraværsperioder">
							{(id: FravaersPerioder) => (
								<>
									<div className="person-visning_content">
										<TitleValue title="Fra og med dato" value={id.fom} />
										<TitleValue title="Til og med dato" value={id.tom} />
									</div>
								</>
							)}
						</DollyFieldArray>
					</>
				)}
			</DollyFieldArray>
		</>
	)
}
