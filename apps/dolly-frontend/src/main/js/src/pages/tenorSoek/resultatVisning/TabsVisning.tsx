import { Tabs } from '@navikt/ds-react'
import { CodeView } from '@/components/codeView'
import React from 'react'
import styled from 'styled-components'

const TabsVisningFormatter = styled.div`
	width: 100%;
	//word-break: break-word;
	&& {
		.navds-tabs__tablist-wrapper {
			margin-bottom: 20px;
		}
	}
	&&& {
		button {
			position: static;
		}
	}
`
export const TabsVisning = ({ children, kildedata }: any) => {
	const kildedataJson = JSON.parse(kildedata)
	const kildedataPretty = JSON.stringify(kildedataJson, null, 2)

	return (
		<TabsVisningFormatter>
			<Tabs defaultValue="nokkelinfo" size="small">
				<Tabs.List>
					<Tabs.Tab value="nokkelinfo" label="NØKKELINFORMASJON" />
					<Tabs.Tab value="kildedata" label="KILDEDATA" />
				</Tabs.List>
				<Tabs.Panel value="nokkelinfo">
					<div className="person-visning_content">{children}</div>
				</Tabs.Panel>
				<Tabs.Panel value="kildedata">
					<CodeView code={kildedataPretty} language="json" />
				</Tabs.Panel>
			</Tabs>
		</TabsVisningFormatter>
	)
}
