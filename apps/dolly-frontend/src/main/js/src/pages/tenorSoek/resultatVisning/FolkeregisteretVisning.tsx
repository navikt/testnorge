import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { arrayToString, codeToNorskLabel, formatDate } from '@/utils/DataFormatter'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import styled from 'styled-components'
import { manualOptions } from '@/pages/tenorSoek/utils'
import { differenceInYears, isDate } from 'date-fns'

const RelasjonerTittel = styled.h3`
	width: 100%;
	border-top: 1px solid #ccc;
	margin-top: 5px;
	padding-top: 15px;
`

export const FolkeregisteretVisning = ({ data }: any) => {
	if (!data) {
		return null
	}

	const foedselsDato = data?.foedselsdato
		? new Date(data.foedselsdato)
		: new Date(data?.foedselsaar)
	const relasjoner = data.tenorRelasjoner?.freg

	return (
		<SubOverskriftExpandable label="Folkeregisteret" iconKind="personinformasjon" isExpanded={true}>
			<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
				<TitleValue title="Identifikator" value={arrayToString(data.identifikator, ', ')} />
				<TitleValue title="Navn" value={data.visningnavn} />
				<TitleValue title="Fødselsdato" value={formatDate(foedselsDato)} />
				<TitleValue
					title="Alder"
					value={isDate(foedselsDato) ? differenceInYears(new Date(), foedselsDato) : undefined}
				/>
				<TitleValue title="Kjønn" value={codeToNorskLabel(data.kjoenn)} />
				<TitleValue title="Personstatus" value={codeToNorskLabel(data.personstatus)} />
				<TitleValue title="Sivilstand" value={codeToNorskLabel(data.sivilstand)} />
				<TitleValue title="Adressebeskyttelse" value={codeToNorskLabel(data.adresseBeskyttelse)} />
				<TitleValue title="Bostedsadresse" value={data.bostedsadresse} />
				<TitleValue
					title="Siste hendelse"
					value={manualOptions[data.sisteHendelse] || codeToNorskLabel(data.sisteHendelse)}
				/>
				{relasjoner?.length > 0 && (
					<>
						<RelasjonerTittel>Relasjoner</RelasjonerTittel>
						{relasjoner.map((relasjon: any, idx: number) => (
							<div className="title-value title-value_small" key={idx + relasjon.identifikator}>
								<h4>{relasjon.tenorRelasjonsnavn}</h4>
								<div>{`${arrayToString(relasjon.identifikator, ', ')} -`}</div>
								<div>{relasjon.visningnavn}</div>
							</div>
						))}
					</>
				)}
			</TabsVisning>
		</SubOverskriftExpandable>
	)
}
