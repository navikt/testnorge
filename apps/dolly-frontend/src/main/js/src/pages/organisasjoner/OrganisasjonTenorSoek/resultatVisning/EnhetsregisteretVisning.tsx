import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import styled from 'styled-components'

const RelasjonerTittel = styled.h3`
	width: 100%;
	border-top: 1px solid #ccc;
	margin-top: 5px;
	padding-top: 15px;
`
export const EnhetsregisteretVisning = ({ data }: any) => {
	if (!data) {
		return null
	}

	return (
		<SubOverskriftExpandable label="Organisasjoninfo" iconKind="organisasjon" isExpanded={true}>
			<TabsVisning kildedata={data.tenorMetadata?.kildedata}>
				<TitleValue title="Organisasjonsnummer" value={data.organisasjonsnummer} />
				<TitleValue title="Navn" value={data.navn} />
			</TabsVisning>
		</SubOverskriftExpandable>
	)
}
