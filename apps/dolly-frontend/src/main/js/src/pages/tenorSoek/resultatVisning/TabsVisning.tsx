import { Tabs } from '@navikt/ds-react'
import { CodeView } from '@/components/codeView'
import React from 'react'
import styled from 'styled-components'
import { FileCodeIcon, KeyVerticalIcon } from '@navikt/aksel-icons'

const TabsVisningFormatter = styled.div`
	width: 100%;
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
					<Tabs.Tab
						value="nokkelinfo"
						label="Nøkkelinformasjon"
						icon={<KeyVerticalIcon title="a11y-title" fontSize="1.5rem" />}
					/>
					<Tabs.Tab
						value="kildedata"
						label="Kildedata"
						icon={<FileCodeIcon title="a11y-title" fontSize="1.5rem" />}
					/>
				</Tabs.List>
				<Tabs.Panel value="nokkelinfo">
					<div className="person-visning_content">{children}</div>
				</Tabs.Panel>
				<Tabs.Panel
					value="kildedata"
					style={{
						display: 'inline-grid',
						width: '100%',
						maxHeight: '600px',
						overflowX: 'auto',
						marginBottom: '15px',
					}}
				>
					<CodeView code={kildedataPretty} language="json" />
				</Tabs.Panel>
			</Tabs>
		</TabsVisningFormatter>
	)
}
