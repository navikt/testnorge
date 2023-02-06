import React from 'react'
import './DataVisning.less'
import 'rc-tooltip/assets/bootstrap_white.css'
import Icon from '@/components/ui/icon/Icon'
import { PdlVisning } from '@/components/fagsystem/pdl/visning/PdlVisning'
import { Ident } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import DollyTooltip from '@/components/ui/button/DollyTooltip'
import { usePdlMiljoeinfo } from '@/utils/hooks/usePdlPerson'

type PdlDataVisningProps = {
	ident: Ident
	bankIdBruker: boolean
	miljoe: string
}

export const ApiFeilmelding = ({ feil }) => {
	return (
		<div className="flexbox--align-center">
			<Icon size={20} kind="report-problem-circle" />
			<div>
				<pre className="api-feilmelding" style={{ fontSize: '1.25em', marginLeft: '5px' }}>
					{feil}
				</pre>
			</div>
		</div>
	)
}

export const PdlDataVisning = ({ ident, bankIdBruker, miljoe }: PdlDataVisningProps) => {
	const getPersonInfo = (hentQ1 = false) => {
		const { pdlData, loading, error } = usePdlMiljoeinfo(ident.ident || ident, hentQ1)
		if (error) {
			return <ApiFeilmelding feil={error} />
		}
		return <PdlVisning pdlData={pdlData} loading={loading} miljoeVisning />
	}

	if (!ident) {
		return null
	}

	return (
		<div className="flexbox--flex-wrap">
			<DollyTooltip
				overlay={getPersonInfo}
				align={{
					offset: [0, -10],
				}}
				arrowContent={<div className="rc-tooltip-arrow-inner" />}
				overlayStyle={{ opacity: 1 }}
				destroyTooltipOnHide={{ keepParent: false }}
			>
				<div className="miljoe-knapp">PDL</div>
			</DollyTooltip>
			{(!bankIdBruker || miljoe === 'q1') && (
				<DollyTooltip
					overlay={() => getPersonInfo(true)}
					align={{
						offset: [0, -10],
					}}
					arrowContent={<div className="rc-tooltip-arrow-inner" />}
					overlayStyle={{ opacity: 1 }}
					destroyTooltipOnHide={{ keepParent: false }}
				>
					<div className="miljoe-knapp">Q1</div>
				</DollyTooltip>
			)}
		</div>
	)
}
