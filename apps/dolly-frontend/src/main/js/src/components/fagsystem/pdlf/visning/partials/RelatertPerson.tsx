import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { allCapsToCapitalized, formatDate, showLabel } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import { PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { PdlDataVisning } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import styled from 'styled-components'
import { sivilstand } from '@/components/fagsystem/pdlf/form/validation/partials'

const StyledPdlData = styled.div`
	margin-bottom: 10px;
	display: flex;
	flex-wrap: wrap;

	p {
		margin: 0;
	}
`

type RelatertPersonData = {
	data: PersonData
	tittel: string
	marginTop: string
}

export const RelatertPerson = ({ data, tittel, marginTop = '0' }: RelatertPersonData) => {
	if (!data) {
		return null
	}

	const getForeldreansvarValues = (foreldreansvar) => {
		return foreldreansvar.map(
			(item, idx) =>
				`${allCapsToCapitalized(item?.ansvar)}: ${item?.ansvarlig}${
					idx + 1 < foreldreansvar.length ? ', ' : ''
				}`,
		)
	}

	return (
		<>
			<div className="person-visning_content">
				<h4 style={{ width: '100%', marginTop: marginTop }}>{tittel}</h4>
				<TitleValue title="Ident" value={data.ident} visKopier />
				<TitleValue title="Fornavn" value={data.navn?.[0].fornavn} />
				<TitleValue title="Mellomnavn" value={data.navn?.[0].mellomnavn} />
				<TitleValue title="Etternavn" value={data.navn?.[0].etternavn} />
				<TitleValue title="Kjønn" value={data.kjoenn?.[0].kjoenn} />
				<TitleValue title="Fødselsdato" value={formatDate(data.foedsel?.[0].foedselsdato)} />
				<TitleValue
					title="Statsborgerskap"
					value={data.statsborgerskap?.[0].landkode}
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
				/>
				<TitleValue
					title="Gradering"
					value={showLabel('gradering', data.adressebeskyttelse?.[0].gradering)}
				/>
				{data.foreldreansvar?.[0].ansvarlig && (
					<TitleValue title="Foreldreansvar" value={getForeldreansvarValues(data.foreldreansvar)} />
				)}
				{data.foreldreansvar?.[0].ansvarligUtenIdentifikator && (
					<TitleValue title="Foreldreansvar" value="Ansvarlig uten identifikator" />
				)}
			</div>
			<StyledPdlData>
				<PdlDataVisning ident={data.ident} />
				<p>
					<i>Hold pekeren over PDL for å se dataene som finnes på {tittel.toLowerCase()} i PDL</i>
				</p>
			</StyledPdlData>
		</>
	)
}
