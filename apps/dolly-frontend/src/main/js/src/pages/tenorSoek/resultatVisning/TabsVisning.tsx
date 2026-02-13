import { Tabs } from '@navikt/ds-react'
import React, { lazy, Suspense } from 'react'
import styled from 'styled-components'
import { FileCodeIcon, KeyVerticalIcon } from '@navikt/aksel-icons'
import Loading from '@/components/ui/loading/Loading'

const TabsVisningFormatter = styled.div`
	width: 100%;

	&& {
		.aksel-tabs__tablist-wrapper {
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
	const PrettyCode = lazy(() => import('@/components/codeView/PrettyCode'))

	if (!kildedata) {
		return <div className="person-visning_content">{children}</div>
	}

	const kildedataIsValidJson = () => {
		try {
			JSON.parse(kildedata)
		} catch (e) {
			return false
		}
		return true
	}

	const kildedataJson = kildedataIsValidJson() ? JSON.parse(kildedata) : null
	const kildedataPretty = kildedataJson ? JSON.stringify(kildedataJson, null, 2) : kildedata

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
						width: '100%',
						maxHeight: '600px',
						overflowX: 'auto',
						scrollbarWidth: 'thin',
						marginBottom: '15px',
					}}
				>
					<Suspense fallback={<Loading label={'Laster kildedata...'} />}>
						<PrettyCode language={'json'} codeString={kildedataPretty} wrapLongLines />
					</Suspense>
				</Tabs.Panel>
			</Tabs>
		</TabsVisningFormatter>
	)
}
