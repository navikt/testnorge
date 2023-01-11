import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { Relasjon, SivilstandData } from '@/components/fagsystem/pdlf/PdlTypes'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { initialSivilstand } from '@/components/fagsystem/pdlf/form/initialValues'
import * as _ from 'lodash-es'

type SivilstandTypes = {
	data: Array<SivilstandData>
	relasjoner: Array<Relasjon>
}

type VisningData = {
	data: SivilstandData
	relasjoner: Array<Relasjon>
}

const SivilstandLes = ({ sivilstandData, relasjoner, idx }) => {
	if (!sivilstandData) {
		return null
	}

	const relatertPersonIdent = sivilstandData.relatertVedSivilstand
	const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === relatertPersonIdent)

	return (
		<div className="person-visning_redigerbar" key={idx}>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue
						title="Type"
						value={Formatters.showLabel('sivilstandType', sivilstandData.type)}
					/>
					<TitleValue
						title="Gyldig fra og med"
						value={
							Formatters.formatDate(sivilstandData.sivilstandsdato) ||
							Formatters.formatDate(sivilstandData.gyldigFraOgMed)
						}
					/>
					<TitleValue
						title="Bekreftelsesdato"
						value={Formatters.formatDate(sivilstandData.bekreftelsesdato)}
					/>
					{!relasjoner && (
						<TitleValue title="Relatert person" value={sivilstandData.relatertVedSivilstand} />
					)}
				</div>
				{relasjon && (
					<RelatertPerson
						data={relasjon.relatertPerson}
						tittel={sivilstandData.type === 'SAMBOER' ? 'Samboer' : 'Ektefelle/partner'}
					/>
				)}
			</ErrorBoundary>
		</div>
	)
}

export const SivilstandVisning = ({ data, relasjoner, idx, tmpPersoner, ident }: VisningData) => {
	const initSivilstand = Object.assign(_.cloneDeep(initialSivilstand), data[idx])
	const initialValues = { sivilstand: initSivilstand }
	//TODO fortsett her!
	// const redigertSivilstandPdlf = _.get(tmpPersoner)

	return (
		<VisningRedigerbarConnector
			dataVisning={<SivilstandLes sivilstandData={data} relasjoner={relasjoner} idx={idx} />}
		/>
	)
}

export const Sivilstand = ({ data, relasjoner, tmpPersoner, ident }: SivilstandTypes) => {
	if (!data || data.length < 1) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Sivilstand (partner)" iconKind="partner" />

			<DollyFieldArray data={data} nested>
				{(sivilstand: SivilstandData, idx: number) => (
					<SivilstandVisning
						key={sivilstand.id}
						data={sivilstand}
						relasjoner={relasjoner}
						idx={idx}
						tmpPersoner={tmpPersoner}
						ident={ident}
					/>
				)}
			</DollyFieldArray>
		</div>
	)
}
