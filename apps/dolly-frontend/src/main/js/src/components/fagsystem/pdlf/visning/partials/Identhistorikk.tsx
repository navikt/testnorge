import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Relasjon } from '@/components/fagsystem/pdlf/PdlTypes'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { getIdenttypeFraIdent, getMasterForIdenttype } from '@/utils/identtype'
import { StyledPdlData } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { PdlDataVisning } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'

type IdenthistorikkVisningProps = {
	relasjoner: Array<Relasjon>
}

export const IdenthistorikkVisning = ({ relasjoner }: IdenthistorikkVisningProps) => {
	const historiskeIdenter = relasjoner?.filter(
		(relasjon) => relasjon.relasjonType === 'GAMMEL_IDENTITET',
	)
	if (!historiskeIdenter || historiskeIdenter.length < 1) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Identhistorikk" iconKind="identhistorikk" />
			<DollyFieldArray data={historiskeIdenter} nested>
				{(relasjon: Relasjon) => {
					const ident = relasjon.relatertPerson?.ident
					const identtype = relasjon.relatertPerson?.identtype ?? getIdenttypeFraIdent(ident)

					return (
						<>
							<div className="person-visning_content">
								<TitleValue title="Ident" value={ident} visKopier />
								<TitleValue title="Identtype" value={identtype} />
								<TitleValue title="Dato" value={formatDate(relasjon.sistOppdatert)} />
								<TitleValue title="Id" value={relasjon.id} />
								<TitleValue title="Master" value={getMasterForIdenttype(identtype)} />
							</div>
							<StyledPdlData>
								<PdlDataVisning ident={ident} />
								<p>
									<i>Hold pekeren over PDL eller Q1 for å se dataene som finnes på gammel ident</i>
								</p>
							</StyledPdlData>
						</>
					)
				}}
			</DollyFieldArray>
		</div>
	)
}
