import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import styled from 'styled-components'
import { arrayToString } from '@/utils/DataFormatter'

const RelasjonerTittel = styled.h3`
	width: 100%;
	border-top: 1px solid #ccc;
	margin-top: 5px;
	padding-top: 15px;
`
export const FolkeregisteretVisning = ({ data }) => {
	if (!data) {
		return null
	}

	const relasjoner = data.tenorRelasjoner?.freg

	return (
		<SubOverskriftExpandable label="Folkeregisteret" iconKind="personinformasjon" isExpanded={true}>
			<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
				<TitleValue title="Identifikator" value={arrayToString(data.identifikator, ', ')} />
				<TitleValue title="Navn" value={data.visningnavn} />
				<TitleValue title="Fødselsdato" value={formatDate(data.foedselsdato)} />
				<TitleValue title="Kjønn" value={showLabel('kjoenn', data.kjoenn)} />
				<TitleValue title="Personstatus" value={showLabel('personstatus', data.personstatus)} />
				<TitleValue title="Sivilstand" value={showLabel('sivilstandType', data.sivilstand)} />
				<TitleValue
					title="Adressebeskyttelse"
					value={showLabel('gradering', data.adresseBeskyttelse)}
				/>
				<TitleValue title="Bostedsadresse" value={data.bostedsadresse} />
				<TitleValue title="Siste hendelse" value={data.sisteHendelse} />
				{relasjoner?.length > 0 && (
					<>
						<RelasjonerTittel>Relasjoner</RelasjonerTittel>
						{relasjoner.map((relasjon, idx) => (
							<div className="title-value title-value_small" key={idx}>
								<h4>{relasjon.tenorRelasjonsnavn}</h4>
								<div>{`${relasjon.identifikator} -`}</div>
								<div>{relasjon.visningnavn}</div>
							</div>
						))}
					</>
				)}
			</TabsVisning>
		</SubOverskriftExpandable>
	)
}