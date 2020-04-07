import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

interface OmsorgspengerVisning {
	data?: Omsorgspenger
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
	if (!data) return null

	return (
		<>
			<h4>Omsorgspenger</h4>
			<div className="person-visning_content">
				<TitleValue title="Har utbetalt pliktige dager" value={data.harUtbetaltPliktigeDager} />
				{data.delvisFravaersListe && (
					<DollyFieldArray data={data.delvisFravaersListe} header="Delvis fravær">
						{(id: DelvisFravaersListe) => (
							<>
								<div className="person-visning_content">
									<TitleValue title="Dato" value={Formatters.formatDate(id.dato)} />
									<TitleValue title="Timer" value={id.timer} />
								</div>
							</>
						)}
					</DollyFieldArray>
				)}

				{data.fravaersPerioder && (
					<DollyFieldArray data={data.fravaersPerioder} header="Delvis fraværsperioder">
						{(id: FravaersPerioder) => (
							<>
								<div className="person-visning_content">
									<TitleValue title="Fra og med dato" value={Formatters.formatDate(id.fom)} />
									<TitleValue title="Til og med dato" value={Formatters.formatDate(id.tom)} />
								</div>
							</>
						)}
					</DollyFieldArray>
				)}
			</div>
		</>
	)
}
