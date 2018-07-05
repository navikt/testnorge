import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import IconButton from '~/components/fields/IconButton/IconButton'
import './table.less'

class TableRow extends PureComponent {
	static propTypes = {
		expandComponent: PropTypes.node,
		navLink: PropTypes.func,
		editAction: PropTypes.func,
		deleteAction: PropTypes.func
	}

	state = {
		expanded: false
	}

	onRowClick = event => {
		const { expandComponent, navLink } = this.props
		if (expandComponent) return this.setState({ expanded: !this.state.expanded })

		if (navLink) return navLink()
	}

	render() {
		const {
			children,
			expandComponent,
			editAction,
			deleteAction,
			navLink,
			...restProps
		} = this.props

		const rowProps = {
			onClick: this.onRowClick,
			...restProps
		}

		const rowClass = cn('dot-body-row', {
			expanded: this.state.expanded
		})

		const iconChevronClass = cn({
			'chevron-down': !this.state.expanded,
			'chevron-up': this.state.expanded
		})

		return (
			<div className={rowClass}>
				<div className="dot-body-row-columns" {...rowProps}>
					{children}
					<Table.Column className="dot-body-row-actioncolumn">
						{editAction && <IconButton kind="edit" onClick={editAction} />}
						{deleteAction && <IconButton kind="trashcan" onClick={deleteAction} />}
						{expandComponent && <IconButton kind={iconChevronClass} onClick={this.onRowClick} />}
					</Table.Column>
				</div>
				{this.state.expanded && (
					<div className="dot-body-row-expandcomponent">{expandComponent}</div>
				)}
			</div>
		)
	}
}

class TableHeader extends PureComponent {
	static propTypes = {
		children: PropTypes.node
	}

	render() {
		const { children, ...restProps } = this.props
		return (
			<div className="dot-header" {...restProps}>
				{children}
			</div>
		)
	}
}

class TableColumn extends PureComponent {
	static propTypes = {
		width: PropTypes.oneOf([
			'10',
			'15',
			'20',
			'25',
			'30',
			'35',
			'40',
			'45',
			'50',
			'55',
			'60',
			'65',
			'70',
			'75',
			'80',
			'85',
			'90',
			'95'
		]),
		value: PropTypes.string
	}

	static defaultProps = {
		width: '10'
	}

	render() {
		const { value, width, children, className, ...restProps } = this.props
		const cssClass = cn('dot-column', `col${width}`, className)

		const render = value ? value : children
		return (
			<div className={cssClass} {...restProps}>
				{render}
			</div>
		)
	}
}

export default class Table extends PureComponent {
	static propTypes = {
		children: PropTypes.node.isRequired
	}

	static Header = TableHeader
	static Row = TableRow
	static Column = TableColumn

	render() {
		const { children, ...restProps } = this.props

		return (
			<div className="dot" {...restProps}>
				{children}
			</div>
		)
	}
}
